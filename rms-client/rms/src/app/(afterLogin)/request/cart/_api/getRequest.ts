export async function getRequest(serviceId: string, sampleId: string) {
    return await fetch(`/w-api/cart-service/services/${serviceId}/samples/${sampleId}`, {
        method: 'GET',
        credentials: 'include',
        cache: 'no-store'
    });
}