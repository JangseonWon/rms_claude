export async function getSampleType(serviceId: string) {
    const res = await fetch(`/w-api/catalog-service/sample_types?service_id=${serviceId}`, {
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