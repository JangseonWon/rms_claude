export async function getSampleType(sampleTypeId: string) {
    const res = await fetch(`/w-api/management-service/sample-types/${sampleTypeId}`, {
        method: 'GET',
        headers: {
            "Content-Type": "application/json",
        },
        credentials: 'include',
        cache: 'no-store'
    });
    if (!res.ok) {
        const errorMessage = await res.text();
        // alert(`Error: ${errorMessage}`);
    }
    return res
}