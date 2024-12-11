import type {Request} from "@/model/Request";

export async function patchRequests(requests: Request[]) {
    const res = await fetch(`/w-api/order-service/requests`, {
        method: 'PATCH',
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