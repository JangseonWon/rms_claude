export async function getServiceGroup(serviceId: string) {
    return await fetch(`/w-api/order-service/services/${serviceId}`, {
        method: 'GET',
        credentials: 'include',
        cache: 'no-store'
    });
}