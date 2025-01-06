export async function getServicesByCategoryId(categoryId: string) {
    return await fetch(`/w-api/catalog-service/services?category_id=${categoryId}`, {
        method: 'GET',
        credentials: 'include',
        cache: 'no-store'
    });
}