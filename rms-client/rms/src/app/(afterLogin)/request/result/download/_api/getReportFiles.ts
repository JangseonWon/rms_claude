export async function getReportFiles(requestIds: string[]) {
    return await fetch(`/w-api/result-service/reports`, {
        method: 'POST',
        headers: {
            "Content-Type": "application/json",
            'Accept': 'application/zip'
        },
        body: JSON.stringify({ ids: requestIds }),
        credentials: 'include',
        cache: 'no-store'
    });
}