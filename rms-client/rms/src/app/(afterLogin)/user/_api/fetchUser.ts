export async function fetchUser(userId: string | undefined) {
    const res = await fetch(`/w-api/management-service/users/${userId}`, {
        method: 'GET',
        credentials: 'include',
        cache: 'no-store'
    });
    if (!res.ok) throw new Error('Failed to fetch data');

    return await res.json();
}