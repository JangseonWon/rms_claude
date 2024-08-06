export async function fetchFile(postId: string, files: File[]) {
    const formData = new FormData();
    files.forEach(file => {
        formData.append('file', file);
    });

    return await fetch(`/w-api/post-service/post/${postId}/file`, {
        method: 'POST',
        body: formData,
        credentials: 'include',
        cache: 'no-store'
    });
}