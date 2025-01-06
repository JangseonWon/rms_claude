export async function getRequestOrderInfo(serviceId: string, sampleId: string) {
    return await fetch(`/w-api/order-service/services/${serviceId}/samples/${sampleId}`, {
        method: 'GET',
        credentials: 'include',
        cache: 'no-store'
    });
}