import {Request} from "@/model/Request"

export async function putRequest(requests: Request[]) {
    const res = await fetch(`/w-api/cart-service/requests`, {
        method: 'PUT',
        headers: {
            "Content-Type": "application/json",
        },
        body: JSON.stringify(requests),
        credentials: 'include',
        cache: 'no-store'
    });
    if (!res.ok) {
        const errorMessage = await res.text();
        // alert(`Error: ${errorMessage}`);
    }
    return res
}