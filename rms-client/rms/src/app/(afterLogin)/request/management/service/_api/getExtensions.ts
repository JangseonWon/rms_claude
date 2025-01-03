export async function getExtensions() {
    return await fetch(`/w-api/management-service/extensions`, {
        method: 'GET',
        credentials: 'include',
        cache: 'no-store'
    });
}