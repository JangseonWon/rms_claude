export async function getServices() {
    return await fetch(`/w-api/management-service/services`, {
        method: 'GET',
        credentials: 'include',
        cache: 'no-store'
    });
}