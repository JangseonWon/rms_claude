/** @type {import('next').NextConfig} */
const nextConfig = {
    async rewrites() {
        return [
            {
                source: '/w-api/:path*',
                destination: `https://rms-test.gcgenome.com/w-api/:path*`, // Matched parameters can be used in the destination
            },
        ]
    },
};
export default nextConfig;
