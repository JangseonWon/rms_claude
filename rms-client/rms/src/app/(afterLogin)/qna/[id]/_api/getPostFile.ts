export async function getPostFile(postFileId: string) {
    const res = await fetch(`/w-api/post-service/post-files/${postFileId}`, {
        method: 'GET',
        credentials: 'include',
        cache: 'no-store'
    });
    if (!res.ok) {
        const errorMessage = await res.text();
        // alert(`Error: ${errorMessage}`);
    }
    return res
}