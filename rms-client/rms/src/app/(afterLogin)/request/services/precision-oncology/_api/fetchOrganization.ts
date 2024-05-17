export async function fetchOrganization(userId: string) {

    const res = await fetch(`/w-api/management-service/users/${userId}/organizations`, {
        method: 'GET',
        credentials: 'include',
        cache: 'no-store'
    });
    if (!res.ok) throw new Error('Failed to fetch data');

    return await res.json();
}