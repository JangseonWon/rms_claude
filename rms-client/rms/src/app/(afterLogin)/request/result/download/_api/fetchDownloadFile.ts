export async function fetchDownloadFile(requestId: string) {
    return await fetch(`/w-api/order-service/requests/reports/${requestId}/file`, {
        method: 'GET',
        credentials: 'include',
        cache: 'no-store'
    });
}