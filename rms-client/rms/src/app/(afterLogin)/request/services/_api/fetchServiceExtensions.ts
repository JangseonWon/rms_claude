export async function fetchServiceExtensions(service: string | undefined) {
    return await fetch(`/w-api/catalog-service/services/${service}/extensions`, {
        method: 'GET',
        credentials: 'include',
        cache: 'no-store'
    });
}