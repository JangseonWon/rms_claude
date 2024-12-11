export async function getServiceGroup(serviceId: string) {
    const res = await fetch(`/w-api/catalog-service/services/${serviceId}`, {
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