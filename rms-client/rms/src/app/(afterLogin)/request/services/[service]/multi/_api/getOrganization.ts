export async function getOrganization(userId: string) {
    const res = await fetch(`/w-api/catalog-service/organizations?user_id=${userId}`, {
        method: 'GET',
        credentials: 'include',
        cache: 'no-store'
    });
    if (!res.ok) throw new Error('Failed to fetch data')
    return  res
}