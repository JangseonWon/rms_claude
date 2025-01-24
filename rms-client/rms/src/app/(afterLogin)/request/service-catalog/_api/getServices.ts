export async function getServices() {
    return await fetch(`/w-api/catalog-service/services`, {
        method: 'GET',
        credentials: 'include',
        cache: 'no-store'
    });
}