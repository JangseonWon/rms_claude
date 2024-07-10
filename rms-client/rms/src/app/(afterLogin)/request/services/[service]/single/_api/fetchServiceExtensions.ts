export async function fetchServiceExtensions(service: string | undefined) {
    const res = await fetch(`/w-api/product-service/services/${service}/extensions`, {
        method: 'GET',
        credentials: 'include',
        cache: 'no-store'
    });
    if (!res.ok) throw new Error('Failed to fetch data');

    return await res.json();
}