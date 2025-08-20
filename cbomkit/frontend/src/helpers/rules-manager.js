import javaRulesData from '../../resources/rules/java-rules.json';
import pythonRulesData from '../../resources/rules/python-rules.json';

/* pmg
Fichero que contiene todas las operaciones relacionadas
con la carga de activos que provienen de los inventarios de reglas
tanto en Python (python_rules.json) como en Java (java_rules.json) */

// Función que construye el árbol de reglas teniendo en cuenta las siguientes longitudes
function buildRuleTree(tree, nodeList, father, language) {
  const actualNode = nodeList[0].trim();
  let idName = "";
  
  if (father === "") { // Nodo raíz
    idName = actualNode;
  }
  else {
    idName = `${father}.${actualNode}`;
  }

  if (nodeList.length > 1) { // Sub-rama
    // Si el nodo no existe, se crea
    if (tree.length === 0 || !tree.some(node => node.id === idName)) {
      tree.push({
        id: idName,
        label: `${actualNode}`,
        children: []
      });
    }
    // Llamamos a la función de forma iterativa pasandole la lista de hijos de ese id
    const subTree = tree.find(node => node.id === idName);
    if (subTree["children"]) {
      buildRuleTree(subTree["children"], nodeList.slice(1), subTree.id, language);
    }
  } else { // Nodo hoja
    // Versión 3: si el lenguaje es python, se le añade al nodo hoja el sufijo
    // _method para aplicarlo en la detección en el plugin
    if (language === 'python') {
      idName += `_method`
    }
    if (!tree.some(child => child.id === idName)) { // Si no existe, lo añadimos
      tree.push({
        id: idName,
        label: `${actualNode}`
      });
    }
  }
}

// Función que lee los .json con las reglas y 
// devuelve una lista con ellas separadas en función del id
function getRulesListFromJson(language) {
  let rulesList = [];

  let jsonFile = null;

  if (language === 'python') {
    jsonFile = pythonRulesData;
  } // Añadir aqui futuros lenguajes
  else {
    jsonFile = javaRulesData;
  }

  // Iteramos por cada regla
  jsonFile.forEach(rule => {
    // Nos quedamos con el primer elemento del id
    const id = rule.id.split('|')[0];
    const groups = id.split('.');

    // Versión 3: en caso de que el lenguaje sea Python, vamos
    // a escoger también el methodName, que lo añadiremos al final
    if (language === 'python') {
      const methodName = rule.methodMatcher.methodNames[0];

      // Si es mayor de 4, omitimos el último elemento
      if (groups.length > 4) {
        groups.pop();
      }

      // Añadimos el nombre del método al final de la lista
      groups.push(methodName);
    }

    rulesList.push(groups);
  });

  return rulesList;
}

// Función para procesar los activos de los json según el lenguage seleccionado
export function processAssets(language) {
  const auxAssetsTree = [];

  const rulesList = getRulesListFromJson(language);

  // Construioms el árbol iterando por cada lista
  rulesList.forEach(rule => buildRuleTree(auxAssetsTree, rule, "", language));

  console.log(`tree for language ${language}: `, auxAssetsTree);

  return auxAssetsTree;
}

