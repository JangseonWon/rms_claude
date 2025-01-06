export async function getOrganization() {
    return await fetch(`/w-api/catalog-service/organizations`, {
        method: 'GET',
        credentials: 'include',
        cache: 'no-store'
    });
}