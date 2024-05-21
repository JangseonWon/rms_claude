export async function getSampleType(serviceId: string) {
    const res = await fetch(`/w-api/cart-service/sample_type?service=${serviceId}`, {
        method: 'GET',
        credentials: 'include',
        cache: 'no-store'
    });
    if (!res.ok) throw new Error('Failed to fetch data')
    return  res
}