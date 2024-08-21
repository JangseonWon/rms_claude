export async function getCategoryById(categoryId: string) {
    return await fetch(`/w-api/management-service/categories/${categoryId}`, {
        method: 'GET',
        credentials: 'include',
        cache: 'no-store'
    });
}