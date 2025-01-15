export async function getRequestRelations() {
    return await fetch(`/w-api/catalog-service/request-relations`, {
        method: 'GET',
        credentials: 'include',
        cache: 'no-store'
    });
}