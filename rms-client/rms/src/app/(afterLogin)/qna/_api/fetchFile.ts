export async function fetchFile(files: File[]) {
    const formData: FormData = new FormData()
    files.forEach(file => {
        formData.append('file', file);
    });

    const res = await fetch(`/w-api/post-service/post/file`, {
        method: 'POST',
        body: formData,
        credentials: 'include',
        cache: 'no-store'
    });
    if (!res.ok) {
        const errorMessage = await res.text();
        // alert(`Error: ${errorMessage}`);
    }
    return res
}