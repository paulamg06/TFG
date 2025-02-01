import React, { useState } from "react";
import {analyzeCrypto} from "../services/Api";

const CryptoAnalyzer = () => {
    const [githubRepo, setGithubRepo] = useState("");
    const [result, setResult] = useState(null);
    const [error, setError] = useState(null);
    const [loading, setLoading] = useState(false);

    const handleAnalyze = async () => {
        setLoading(true);
        setError(null);
        setResult(null);

        try {
            const response = await analyzeCrypto(githubRepo);
            if (response.error) {
                setError(response.error);
            } else {
                setResult(response);
            }
        } catch (error) {
            setError(error.message);
        }

        setLoading(false);
    };

    return (
        <div>
            <h1>Crypto Analyzer</h1>
            <input
                type="text"
                placeholder="Github repository"
                value={githubRepo}
                onChange={(e) => setGithubRepo(e.target.value)}
            />
            <button onClick={handleAnalyze} disabled={loading}>
                {loading ? "Analyzing..." : "Analyze"}
            </button>
            {error && <p style={{ color: "red" }}>{error}</p>}
            {result && (
                <pre>{JSON.stringify(result, null, 2)}</pre>
            )}
        </div>
    );
};

export default CryptoAnalyzer;