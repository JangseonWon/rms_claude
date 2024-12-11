import {Query} from "@/model/Query";

export async function postSampleTypes(search: Query) {
    const res = await fetch(`/w-api/management-service/sample-types/search`, {
        method: 'POST',
        headers: {
            "Content-Type": "application/json",
        },
        body: JSON.stringify(search),
        credentials: 'include',
        cache: 'no-store'
    });
    if (!res.ok) {
        const errorMessage = await res.text();
        // alert(`Error: ${errorMessage}`);
    }
    return res
}