export async function fetchOrganization() {
    const res = await fetch(`/w-api/management-service/users/220008/organizations`, {
        method: 'GET',
        credentials: 'include',
        cache: 'no-store'
    });
    if (!res.ok) throw new Error('Failed to fetch data');

    return await res.json();
}