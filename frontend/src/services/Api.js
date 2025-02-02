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
            console.log(response);
            throw new Error(`An error has occured: ${response.statusText} ${response.status}`);
        }

        return await response.json();
    }
    catch (error) {
        console.error("Error:", error);
        return { error: error.message };
    }
};