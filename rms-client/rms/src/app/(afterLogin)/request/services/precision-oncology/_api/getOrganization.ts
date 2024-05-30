export async function getOrganization() {
    return  await fetch(`/w-api/product-service/organizations`, {
        method: 'GET',
        credentials: 'include',
        cache: 'no-store'
    });
}