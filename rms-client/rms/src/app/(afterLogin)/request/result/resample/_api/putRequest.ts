import {Request} from "@/model/Request";

export async function putRequest(request: Request, rootServiceId: String, rootSampleId: String) {
    return await fetch(`/w-api/result-service/requests/resample?service_id=${rootServiceId}&sample_id=${rootSampleId}`, {
        method: 'PUT',
        headers: {
            "Content-Type": "application/json",
        },
        body: JSON.stringify(request),
        credentials: 'include',
        cache: 'no-store'
    });
}