export async function getUser(userId: string) {
    return await fetch(`/w-api/management-service/users/${userId}`, {
        method: 'GET',
        credentials: 'include',
        cache: 'no-store'
    });
}