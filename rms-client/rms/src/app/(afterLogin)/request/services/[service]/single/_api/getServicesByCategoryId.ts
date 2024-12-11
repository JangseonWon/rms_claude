export async function getServicesByCategoryId(categoryId: string) {
    const res = await fetch(`/w-api/catalog-service/services?category_id=${categoryId}`, {
        method: 'GET',
        credentials: 'include',
        cache: 'no-store'
    });
    if (!res.ok) {
        const errorMessage = await res.text();
        // alert(`Error: ${errorMessage}`);
    }
    return res
}