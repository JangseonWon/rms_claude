export async function getService(serviceId: string) {
    return await fetch(`/w-api/management-service/services/${serviceId}`, {
        method: 'GET',
        credentials: 'include',
        cache: 'no-store'
    });
}