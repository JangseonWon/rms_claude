export async function getService(serviceId: string) {
    const res = await fetch(`/w-api/management-service/services/${serviceId}`, {
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