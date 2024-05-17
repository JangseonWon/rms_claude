export async function getRequest(orderId: string, serviceId: string, sampleId: string) {
    const res = await fetch(`/w-api/cart-service/orders/${orderId}/services/${serviceId}/samples/${sampleId}`, {
        method: 'GET',
        credentials: 'include',
        cache: 'no-store'
    });
    if (!res.ok) throw new Error('Failed to fetch data')
    return  res
}