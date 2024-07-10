export async function fetchServiceSampleFileDownload(serviceName: string) {
    return await fetch(`/w-api/order-service/requests/services/${serviceName}/file`, {
        method: 'GET',
        credentials: 'include',
        cache: 'no-store'
    });
}