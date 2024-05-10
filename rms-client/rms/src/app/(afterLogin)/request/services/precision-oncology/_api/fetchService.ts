export async function fetchService() {
    const res = await fetch(`/w-api/product-service/categories/38fecf42-1404-490f-ab97-37ed7eeecd78/services`, {
        method: 'GET',
        credentials: 'include',
        cache: 'no-store'
    });
    if (!res.ok) throw new Error('Failed to fetch data');

    return await res.json();
}