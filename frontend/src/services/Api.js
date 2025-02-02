const URL_API = 'http://localhost:5000';

export const analyzeCrypto = async (github_repo) => {
    try {
        const response = await fetch(`${URL_API}/api/analyzeCrypto`, {
            method: 'POST',
            headers: {
                'Content-Type': 'application/json',
            },
            body: JSON.stringify({ github_repo: github_repo }),
        });

        if (!response.ok) {
            const error = await response.json();
            throw new Error(`Error: ${error.error}`);
        }

        return await response.json();
    }
    catch (error) {
        console.error("Error:", error);
        return { error: error.message };
    }
};