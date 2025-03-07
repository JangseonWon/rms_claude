export async function getCategories() {
    return await fetch(`/w-api/catalog-service/categories`, {
        method: 'GET',
        credentials: 'include',
        cache: 'no-store'
    });
}