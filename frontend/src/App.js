import React from "react";
import "./App.css";
import CryptoAnalyzer from "./components/CryptoAnalyzer";

function App() {
  return (
    <div className="App">
      <header className="App-header">
        <div></div>
        <h1>Analizador de seguridad de repositorios de GitHub</h1>
      </header>
      <CryptoAnalyzer />
    </div>
  );
}

export default App;

