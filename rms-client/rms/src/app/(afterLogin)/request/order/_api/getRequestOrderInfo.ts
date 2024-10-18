export async function getRequestOrderInfo(serviceId: string, sampleId: string) {
    const res = await fetch(`/w-api/order-service/services/${serviceId}/samples/${sampleId}`, {
        method: 'GET',
        credentials: 'include',
        cache: 'no-store'
    });
    if (!res.ok) throw new Error('Failed to fetch data')
    return  res
}