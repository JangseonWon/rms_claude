/** @type {import('next').NextConfig} */
const nextConfig = {
    async rewrites() {
        return [
            {
                source: '/w-api/:path*',
                destination: `http://localhost:4444/w-api/:path*`, // Matched parameters can be used in the destination
            },
        ]
    },
};
export default nextConfig;
