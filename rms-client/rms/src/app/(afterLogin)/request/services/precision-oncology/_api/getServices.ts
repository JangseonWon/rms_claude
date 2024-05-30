export async function getServices() {
    return await fetch(`/w-api/product-service/services?category_id=38fecf42-1404-490f-ab97-37ed7eeecd78`, {
        method: 'GET',
        credentials: 'include',
        cache: 'no-store'
    });
}