export async function fetchMultiDownloadFile(requestIds: Array<string>) {
    return await fetch(`/w-api/order-service/requests/reports/multi-download`, {
        method: 'POST',
        headers: {
            "Content-Type": "application/json",
        },
        body: JSON.stringify({ ids: requestIds }),
        credentials: 'include',
        cache: 'no-store'
    });
}