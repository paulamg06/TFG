import { model, ErrorStatus } from "@/model.js";
import { API_LAST_CBOM_URL, API_CHECK_POLICY } from "@/app.config";
import { checkValidComplianceResults, createLocalComplianceReport, isViewerOnly } from "@/helpers.js";
import javaRulesData from '../../resources/rules/java_rules.json';
import pythonRulesData from '../../resources/rules/python_rules.json';


export function fetchLastCboms(number) {
  let apiUrl = `${API_LAST_CBOM_URL}/${number}`;
  fetchDataFromApi(apiUrl, null)
    .then((jsonData) => {
      model.lastCboms = jsonData;
      if (Array.isArray(jsonData) && jsonData.length === 0) {
        model.addError(ErrorStatus.EmptyDatabase);
      }
    })
    .catch((error) => {
      console.error("Error:", error.message);
      model.addError(ErrorStatus.NoConnection);
    });
}

function getLocalComplianceReport(cbom) {
  let jsonDataLocal = createLocalComplianceReport(cbom);
  if (checkValidComplianceResults(jsonDataLocal)) {
    model.policyCheckResult = jsonDataLocal;
  } else {
    model.policyCheckResult = { error: true };
  }
}

function getRemoteComplianceReport(cbom, policyIdentifier = 'quantum_safe') {
  const apiUrl = `${API_CHECK_POLICY}?policyIdentifier=${policyIdentifier}`;

  // Create the request options
  const requestOptions = {
    method: "POST",
    headers: {
      "Content-Type": "application/json",
    },
    body: JSON.stringify(cbom),
  };

  // Make the POST request
  fetchDataFromApi(apiUrl, requestOptions)
    .then((jsonData) => {
      if (checkValidComplianceResults(jsonData)) {
        model.policyCheckResult = jsonData;
      } else {
        // An error occured in the backend compliance service, we use the local compliance service instead
        console.warn("Using the local compliance report instead of the remote one")
        model.addError(ErrorStatus.FallBackLocalComplianceReport)
        getLocalComplianceReport(cbom)
      }
    })
    .catch(() => {
      console.warn("Using the local compliance report instead of the remote one")
      model.addError(ErrorStatus.FallBackLocalComplianceReport)
      getLocalComplianceReport(cbom)
    });
}

export function getComplianceReport(cbom, policyIdentifier = 'quantum_safe') {
  if (isViewerOnly()) {
    getLocalComplianceReport(cbom)
  } else {
    getRemoteComplianceReport(cbom, policyIdentifier)
  }
}

function fetchDataFromApi(apiUrl, requestOptions) {
  let fetchPromise;
  if (requestOptions === null) {
    fetchPromise = fetch(apiUrl);
  } else {
    fetchPromise = fetch(apiUrl, requestOptions);
  }
  return fetchPromise
    .then((response) => {
      if (!response.ok) {
        throw new Error(`HTTP error! Status: ${response.status}`);
      }
      return response.json();
    })
    .then((data) => {
      // console.log(`Received data from ${apiUrl}:`, data)
      return data;
    })
    .catch((error) => {
      // Handle errors during the fetch
      console.error("Error during request:", error.message);
      throw error;
    });
}

// pmg: función para obtener los activos de python_rules.json
export function getPythonAssets() {
  const auxAssetsDict = {};

  // Iteramos por cada regla del fichero
  pythonRulesData.forEach(rule =>{
    const id = rule.id.split('|')[0];
    const auxList = id.split('.');
    let group = auxList[3];
    const methodName = rule.methodMatcher.methodNames[0];

    // Si id de ese conjunto es menor de 4 elementos, va a obtener defined,
    // por lo que se obtiene el último elemento.
    if (group === undefined){
      group = auxList.at(-1);
    }

    if (group in auxAssetsDict){
      if (!auxAssetsDict[group].includes(methodName)){
        auxAssetsDict[group].push(methodName);
      }
    }
    else {
      // Si no existe el grupo, se inicializa la lista
      auxAssetsDict[group] = [methodName];
    }
  });

  // Limpiamos las claves
  const assetsDict = {};
  Object.keys(auxAssetsDict).forEach(key => {
    assetsDict[key.trim()] = auxAssetsDict[key];
  });

  return assetsDict;
}

// pmg: método que se encarga de procesar los activos de Python
export function processPythonAssets() {
  // Obtenemos el diccionario con todos los assets
  const pythonAssets = getPythonAssets();
  console.log(pythonAssets);

  const assetList = []

  // Iteramos por cada clave del diccionario para obtener cada agrupación
  for (const group in pythonAssets){
    const methodsList = [];

    // Iteramos por cada elemento de cada agrupación para almacenarla en un diccionario
    pythonAssets[group].forEach(method => {
      const methodDict = {
        id: `${group}_${method}`,
        label: method
      };
      methodsList.push(methodDict);
    });

    // Diccionario con los activos de cada agrupación
    const dictGroup = {
      id: group,
      label: group,
      children: methodsList
    };

    assetList.push(dictGroup);
  }

  console.log("Lista: ", assetList);

  return assetList;
}



// pmg: función para obtener los activos de java_rules.json
export function getJavaAssets(){
  const assetsDict = {};

  javaRulesData.forEach(rule =>{
    assetsDict[rule.id] = rule.methodMatcher;
  });

  return assetsDict;
}
