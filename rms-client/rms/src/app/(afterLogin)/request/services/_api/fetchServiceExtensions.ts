export async function fetchServiceExtensions(service: string | undefined) {
    const res = await fetch(`/w-api/catalog-service/services/${service}/extensions`, {
        method: 'GET',
        credentials: 'include',
        cache: 'no-store'
    });
    if (!res.ok) {
        const errorMessage = await res.text();
        // alert(`Error: ${errorMessage}`);
    }
    return res.json();
}