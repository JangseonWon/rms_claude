export async function patchExtension(extensionId: string, regex: string) {
    return await fetch(`/w-api/management-service/extension/${extensionId}`, {
        method: 'PATCH',
        headers: {
            "Content-Type": "application/json",
        },
        body: JSON.stringify({
            regex: regex
        }),
        credentials: 'include',
        cache: 'no-store'
    });
}