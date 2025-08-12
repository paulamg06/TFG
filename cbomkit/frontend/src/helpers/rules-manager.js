import javaRulesData from '../../resources/rules/java-rules.json';
import pythonRulesData from '../../resources/rules/python-rules.json';

/* pmg
Fichero que contiene todas las operaciones relacionadas
con la carga de activos que provienen de los inventarios de reglas
tanto en Python (python_rules.json) como en Java (java_rules.json) */

// Función que construye el árbol de reglas
function buildRuleTree(tree, nodeList, father) {
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
      buildRuleTree(subTree["children"], nodeList.slice(1), subTree.id);
    }
  } else { // Nodo hoja
    if (!tree.some(child => child.id === idName)) { // Si no existe, lo añadimos
      tree.push({
        id: idName,
        label: `${actualNode}`
      });
    }
  }
}

// Función que lee los .json con las reglas y devuelve una lista con ellas separadas en función del id
function getAllJsonRules(language) {
  let file = null;
  // Añadir aqui futuros lenguajes
  if (language === 'python') {
    file = pythonRulesData;
  }
  else {
    file = javaRulesData;
  }

  if (file === null) {
    return [];
  }

  let rulesList = [];

  // Iteramos por cada regla
  file.forEach(rule => {
    // Nos quedamos con el primer elemento del id
    const id = rule.id.split('|')[0];
    const groups = id.split('.');

    rulesList.push(groups);
  });

  return rulesList;
}

// Función para procesar los activos de los json según el lenguage seleccionado
export function processAssets(language) {
  const auxAssetsTree = [];
  const rulesList = getAllJsonRules(language);

  if (rulesList.length === 0) {
    console.warn(`No rules found for language: ${language}`);
    return auxAssetsTree;
  }

  // Construioms el árbol iterando por cada lista
  rulesList.forEach(rule => buildRuleTree(auxAssetsTree, rule, ""));

  console.log(`tree for language ${language}: `, auxAssetsTree);

  return auxAssetsTree;
}
