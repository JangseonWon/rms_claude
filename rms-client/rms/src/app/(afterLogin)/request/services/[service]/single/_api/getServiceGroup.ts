export async function getServiceGroup(serviceId: string) {
    return await fetch(`/w-api/catalog-service/services/${serviceId}`, {
        method: 'GET',
        credentials: 'include',
        cache: 'no-store'
    });
}