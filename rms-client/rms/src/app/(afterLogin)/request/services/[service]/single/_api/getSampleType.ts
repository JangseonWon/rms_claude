export async function getSampleType(serviceId: string) {
    return await fetch(`/w-api/product-service/sample_types?service_id=${serviceId}`, {
        method: 'GET',
        credentials: 'include',
        cache: 'no-store'
    });
}