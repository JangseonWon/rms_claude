export async function getCategories() {
    return await fetch(`/w-api/management-service/categories`, {
        method: 'GET',
        credentials: 'include',
        cache: 'no-store'
    });
}