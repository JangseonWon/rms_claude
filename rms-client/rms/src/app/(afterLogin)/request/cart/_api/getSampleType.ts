export async function getSampleType(serviceId: string) {
    return await fetch(`/w-api/cart-service/sample_types?service_id=${serviceId}`, {
        method: 'GET',
        credentials: 'include',
        cache: 'no-store'
    });
}