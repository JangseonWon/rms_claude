export async function getService(serviceId: string) {
    return await fetch(`/w-api/catalog-service/service/${serviceId}`, {
        method: 'GET',
        credentials: 'include',
        cache: 'no-store'
    });
}