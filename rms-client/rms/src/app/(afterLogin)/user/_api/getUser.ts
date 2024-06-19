export async function getUser(userId: string | undefined) {
    return await fetch(`/w-api/management-service/users/${userId}`, {
        method: 'GET',
        credentials: 'include',
        cache: 'no-store'
    });
}